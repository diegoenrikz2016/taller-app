import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ServicioService } from '../service/servicio.service';
import { IServicio } from '../servicio.model';

import { ServicioFormService } from './servicio-form.service';
import { ServicioUpdate } from './servicio-update';

describe('Servicio Management Update Component', () => {
  let comp: ServicioUpdate;
  let fixture: ComponentFixture<ServicioUpdate>;
  let activatedRoute: ActivatedRoute;
  let servicioFormService: ServicioFormService;
  let servicioService: ServicioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(ServicioUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    servicioFormService = TestBed.inject(ServicioFormService);
    servicioService = TestBed.inject(ServicioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const servicio: IServicio = { id: 644 };

      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      expect(comp.servicio).toEqual(servicio);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IServicio>();
      const servicio = { id: 24037 };
      vitest.spyOn(servicioFormService, 'getServicio').mockReturnValue(servicio);
      vitest.spyOn(servicioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(servicio);
      saveSubject.complete();

      // THEN
      expect(servicioFormService.getServicio).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(servicioService.update).toHaveBeenCalledWith(expect.objectContaining(servicio));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IServicio>();
      const servicio = { id: 24037 };
      vitest.spyOn(servicioFormService, 'getServicio').mockReturnValue({ id: null });
      vitest.spyOn(servicioService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicio: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(servicio);
      saveSubject.complete();

      // THEN
      expect(servicioFormService.getServicio).toHaveBeenCalled();
      expect(servicioService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IServicio>();
      const servicio = { id: 24037 };
      vitest.spyOn(servicioService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(servicioService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
